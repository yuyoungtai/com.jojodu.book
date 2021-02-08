package com.jojodu.book.springboot.config.auth;

import com.jojodu.book.springboot.config.auth.dto.OAuthAttributes;
import com.jojodu.book.springboot.config.auth.dto.SessionUser;
import com.jojodu.book.springboot.domain.user.Role;
import com.jojodu.book.springboot.domain.user.User;
import com.jojodu.book.springboot.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Collections;

    @RequiredArgsConstructor
    @Service
    public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User>{
        private final UserRepository userRepository;
        private final HttpSession httpSession;

        @Override
        public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException{

            OAuth2UserService delegate = new DefaultOAuth2UserService();

            //OAuth2User에 소셜 로그인 정보가 담겨 있음. Attributes를 가지고 email, picture, name을 가져올 수 있음.
            OAuth2User oAuth2User = delegate.loadUser(userRequest);

            //네이버인지 구글 로그인인지 식별하기 위한 값.
            String registrationId = userRequest.getClientRegistration().getRegistrationId();

            //OAuth2 로그인 진행시 키가 되는 필드 값
            //구글의 경우 기본적으로 지원(기본코드 sub), 네이버 및 카카오 미지원
            //이후 네이버와 구글 로그인 동시 지원할 때 사용 예정
            String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint()
                    .getUserNameAttributeName();

            //OAuthAttributes.of 함수는 구글 로그인 정보를 토대로 OAuthattribute 객체를 생성
            OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

            //구글 로그인 정보(attributes)에 name 혹은 picture 가 갱신되면 업데이트 아니면 User 엔티티 반환.
            //로그인하면 이 매소드에서 DB 업데이트 혹은 save가 이루어짐.
            User user = saveOrUpdate(attributes);

            //기존 User 클래스를 사용하지 않고 별도 세전에 사용자 정보를 저장하기 위한 Dto 클래스(SessionUser)
            //로그인 성공시 세션에 SessionUser 클래스의 사용자 정보를 저장한다.
            httpSession.setAttribute("user", new SessionUser(user));

            return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
                    attributes.getAttributes(),attributes.getNameAttributeKey());
        }

        private User saveOrUpdate(OAuthAttributes attributes){
            User user = userRepository.findByEmail(attributes.getEmail())
                    .map(entity -> entity.update(attributes.getName(), attributes.getPicture()))
                    .orElse(attributes.toEntity());

            return userRepository.save(user);
        }
    }

