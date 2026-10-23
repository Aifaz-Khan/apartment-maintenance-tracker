package com.example.maintenance.shared.security;
import com.example.maintenance.shared.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.*;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
 private final ObjectMapper mapper; public CustomAccessDeniedHandler(ObjectMapper mapper){this.mapper=mapper;}
 @Override public void handle(HttpServletRequest req,HttpServletResponse res,AccessDeniedException ex)throws IOException {
  res.setStatus(403); res.setContentType(MediaType.APPLICATION_JSON_VALUE); mapper.writeValue(res.getOutputStream(),new ErrorResponse(403,"FORBIDDEN","You do not have permission to access this resource.",req.getRequestURI()));
 }
}
