package com.restaurant.security;
import com.restaurant.entity.User; import com.restaurant.exception.ForbiddenException; import org.springframework.security.core.Authentication; import org.springframework.security.core.context.SecurityContextHolder; import org.springframework.stereotype.Service;
@Service
public class CurrentUserService {
 public User getCurrentUser(){ Authentication auth=SecurityContextHolder.getContext().getAuthentication(); if(auth==null || !(auth.getPrincipal() instanceof SecurityUser su)) throw new ForbiddenException("Login required"); return su.getUser(); }
 public Long getCurrentUserId(){ return getCurrentUser().getUserId(); }
}
