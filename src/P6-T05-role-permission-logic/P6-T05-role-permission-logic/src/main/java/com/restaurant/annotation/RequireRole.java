package com.restaurant.annotation;

import com.restaurant.constant.AppRole;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.access.prepost.PreAuthorize;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("@authorizationService.hasAnyRole(authentication, #this.value)")
public @interface RequireRole {
    AppRole[] value();
}
