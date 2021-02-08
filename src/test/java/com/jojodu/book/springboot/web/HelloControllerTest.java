package com.jojodu.book.springboot.web;

import com.jojodu.book.springboot.config.auth.SecurityConfig;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = HelloController.class,excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)})

public class HelloControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @WithMockUser(roles="USER")
    public void hello가_리턴된다() throws Exception {
        String hello = "hello";

        mvc.perform(get("/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string(hello));
    }


    @Test
    @WithMockUser(roles="USER")
    public void helloDto가_리턴된다() throws Exception{
        String name = "hello";
        int amount = 1000;

        //HelloControoler 클래스의 helloDto 매소드 테스트.
        //Get 요청으로 name, amount 값을 파라미터로 전달하면 해당 값으로 HelloResponseDto 클래스를 생성하고
        //생성한 클래스를 반환한다.
        //검증을 jsonPath로 하는데 이유는 RestController는 json을 반환하는 컨트롤러이기 때문
        //jsonPath는 응답값을 필드별로 검증할 수 있게 해주는 메소드. $를 기준으로 필드명을 명시함.
        //결국 'name, amount' 값을 파라미터로 넘기고 해당 값으로 HelloResponseDto 클래스를 제대로 생성했는지 체크함.

        mvc.perform(get("/hello/dto").param("name", name)
                .param("amount", String.valueOf(amount)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(name)))
                .andExpect(jsonPath("$.amount",is(amount)));
    }
}
