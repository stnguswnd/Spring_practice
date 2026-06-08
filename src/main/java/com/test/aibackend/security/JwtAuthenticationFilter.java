package com.test.aibackend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 모든 요청에서 1회 실행되어 Authorization 헤더의 JWT를 검증합니다.
 *
 * 검증 성공 시 UserDetails 를 principal 로 세팅하여 컨트롤러에서
 * {@code @AuthenticationPrincipal UserDetails user} 로 받을 수 있도록 합니다.
 * (Form 로그인 경로와 principal 타입 일관성을 유지합니다.)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String HEADER = "Authorization";
    private static final String PREFIX = "Bearer "; //신원 인증할 때 쓰는 베어러 타입이 있따.

    private final JwtUtil jwtUtil; //생성, 검증 Bean 객체를 주입을 받아온다.
    private final UserDetailsService userDetailsService; //기존에 시큐리티를 구현체로 만들어온걸 가져온다.

    @Override  //doFilterInternal 안에는 request, response, filter chain 등을 가져와서 쓴다
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader(HEADER);
        if (header != null && header.startsWith(PREFIX)) { //베어러로 시작하는 정상 토큰인지 먼저 본다.
            String token = header.substring(PREFIX.length()); //베어러는 뺴버리고, 순수 코큰을 보고 놀겠다 라는 의미
            try {
                Claims claims = jwtUtil.parse(token); //우리가 받아온 토큰을 던져서 검증
                String username = claims.getSubject(); //서브젝트에 유저네임 넣고
                UserDetails userDetails = userDetailsService.loadUserByUsername(username); //유저 디테일 객체를 만들어서 컨텍스트 홀더에 담는다.
                //애는 유저 네임을 받아서 읽고, 유저가 없으면 에러를 띄우고,~ 객체값을 컨텍스트 홀더에 집어 넣는다.

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth); //auth를 컨텍스트 홀더 안에 인증객체로 넣어준다.
            } catch (JwtException e) {
                log.debug("JWT verification failed: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            } catch (UsernameNotFoundException e) {
                log.debug("user from JWT not found: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }
        chain.doFilter(request, response); //아 jwt 인증객체가 검증이 성공했으니, 담자, 근데 이걸 어디다가 끼울 것인가?
    }
}
