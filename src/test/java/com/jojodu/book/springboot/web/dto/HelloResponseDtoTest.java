package com.jojodu.book.springboot.web.dto;

import org.junit.*;

import static org.assertj.core.api.Assertions.assertThat;

public class HelloResponseDtoTest {

    @Test
    public void 롬복_기능_테스트(){
        String name = "test";
        int amount = 1000;

        //HelloResponseDto의 생성자가 동작하는지 체크. @RequiredArgsConstructor 애노테이션이 자동으로 생성자를 생성해줌.
        HelloResponseDto dto = new HelloResponseDto(name,amount);

        //아래 테스트 코드로 HelloResponseDto 클래스의 생성자와 초기 값이 제대로 들어갔는지 확인할 수 있음.
        assertThat(dto.getName()).isEqualTo(name);
        assertThat(dto.getAmount()).isEqualTo(amount);
    }
}
