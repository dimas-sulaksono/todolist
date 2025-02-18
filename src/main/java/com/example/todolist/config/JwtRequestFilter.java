package com.example.todolist.config;

import com.example.todolist.Util.JwtUtil;
import com.example.todolist.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;

    private final UserService userService;

    @Autowired
    public JwtRequestFilter(UserService userService) {
        this.userService = userService;
    }

    // method doFilterInternal untuk memfilter setiap requet yang masuk
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // ambil header token
        String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // ambil informasi token
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer")){
            jwt = authorizationHeader.substring(7); // informasi token jwt setelah urutan ke 7
            username = jwtUtil.extractUsername(jwt); /// ambil username dari toke yang diekstrak
        }

        // validasi user
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            // nampung object yang isinya innformasni uname, pass, roe/otoritas
            UserDetails userDetails = this.userService.loadUserByUsername(username);

            // nampung object yang isinya adalah user yang sudah divaldasi
            if(jwtUtil.validateToken(jwt, userDetails)){
                // nampung object yang isinya informasi uname, pass, role/otoritas
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                // buat nambahin detail informasi dari request yang dikirim
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // menetapkan user telah terauntentikasi dan terotorisasi
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        // buat jalanin konfigurasi filter
        filterChain.doFilter(request, response);
    }
}