package com.restaurant.security;
import com.restaurant.service.CustomUserDetailsService; import jakarta.servlet.FilterChain; import jakarta.servlet.ServletException; import jakarta.servlet.http.*; import lombok.RequiredArgsConstructor; import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; import org.springframework.security.core.context.SecurityContextHolder; import org.springframework.security.web.authentication.WebAuthenticationDetailsSource; import org.springframework.stereotype.Component; import org.springframework.web.filter.OncePerRequestFilter; import java.io.IOException;
@Component @RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
 private final JwtService jwtService; private final CustomUserDetailsService userDetailsService;
 @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
  String header=request.getHeader("Authorization");
  if(header!=null && header.startsWith("Bearer ") && SecurityContextHolder.getContext().getAuthentication()==null){
   String token=header.substring(7); try{ String email=jwtService.extractEmail(token); SecurityUser user=(SecurityUser)userDetailsService.loadUserByUsername(email); if(jwtService.isValid(token,user)){ UsernamePasswordAuthenticationToken auth=new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities()); auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); SecurityContextHolder.getContext().setAuthentication(auth); } } catch(Exception ignored){}
  }
  filterChain.doFilter(request,response);
 }
}
