package com.example.maintenance.shared.security;
import com.example.maintenance.shared.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.*;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
 private final ObjectMapper mapper; public CustomAuthenticationEntryPoint(ObjectMapper mapper){this.mapper=mapper;}
 @Override public void commence(HttpServletRequest req,HttpServletResponse res,AuthenticationException ex)throws IOException {
  res.setStatus(401); res.setContentType(MediaType.APPLICATION_JSON_VALUE); mapper.writeValue(res.getOutputStream(),new ErrorResponse(401,"UNAUTHORIZED","Authentication is required.",req.getRequestURI()));
 }
}
