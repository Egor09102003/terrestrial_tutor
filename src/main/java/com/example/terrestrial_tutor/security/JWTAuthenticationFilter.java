package com.example.terrestrial_tutor.security;

import com.example.terrestrial_tutor.entity.AdminEntity;
import com.example.terrestrial_tutor.entity.PupilEntity;
import com.example.terrestrial_tutor.entity.SupportEntity;
import com.example.terrestrial_tutor.entity.TutorEntity;
import com.example.terrestrial_tutor.entity.enums.ERole;
import com.example.terrestrial_tutor.repository.SupportRepository;
import com.example.terrestrial_tutor.service.AdminDetailsService;
import com.example.terrestrial_tutor.service.PupilDetailsService;
import com.example.terrestrial_tutor.service.SupportDetailsService;
import com.example.terrestrial_tutor.service.TutorDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

public class JWTAuthenticationFilter extends OncePerRequestFilter {

    public static final Logger LOG = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    @Autowired
    private JWTTokenProvider jwtTokenProvider;
    @Autowired
    PupilDetailsService pupilDetailsService;
    @Autowired
    TutorDetailsService tutorDetailsService;
    @Autowired
    AdminDetailsService adminDetailsService;
    @Autowired
    SupportDetailsService supportDetailsService;

    private String getJWTFromRequest(HttpServletRequest request) {
        String reqToken = request.getHeader(SecurityConstants.HEADER_STRING);
        if (StringUtils.hasText(reqToken) && reqToken.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            return reqToken.split(" ")[1];
        }
        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJWTFromRequest(request);
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                Long userId = jwtTokenProvider.getUserIdFromToken(jwt);
                ERole userRole = jwtTokenProvider.getUserRoleFromToken(jwt);
                UsernamePasswordAuthenticationToken authentication;
                if (userRole == ERole.PUPIL) {
                    PupilEntity pupilDetails = pupilDetailsService.loadPupilById(userId);
                    authentication = new UsernamePasswordAuthenticationToken(
                            pupilDetails, null, Collections.emptyList()
                    );
                } else if (userRole == ERole.TUTOR){
                    TutorEntity tutorDetails = tutorDetailsService.loadTutorById(userId);
                    authentication = new UsernamePasswordAuthenticationToken(
                            tutorDetails, null, Collections.emptyList()
                    );
                } else if (userRole == ERole.SUPPORT){
                    SupportEntity supportDetails = supportDetailsService.loadSupportById(userId);
                    authentication = new UsernamePasswordAuthenticationToken(
                            supportDetails, null, Collections.emptyList()
                    );
                } else {
                    AdminEntity adminDetail = adminDetailsService.loadAdminById(userId);
                    authentication = new UsernamePasswordAuthenticationToken(
                            adminDetail, null, Collections.emptyList()
                    );
                }
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}
