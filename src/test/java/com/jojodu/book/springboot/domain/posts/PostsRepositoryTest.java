package com.jojodu.book.springboot.domain.posts;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PostsRepositoryTest {

    @Autowired
    PostsRepository postsRepository;

    //Junit에서 단위 테스트가 끝날 때마다 수행되는 메소드 지정.
    @After
    public void cleanup(){
        postsRepository.deleteAll();
    }

    @Test
    public void 게시글저장_불러오기(){

        String title = "테스트 게시글";
        String content = "테스트 본문";

        postsRepository.save(Posts.builder()
        .title(title)
        .content(content)
        .author("jojodu@gmail.com")
        .build());


        List<Posts> postsList = postsRepository.findAll();

        Posts posts = postsList.get(0);
        assertThat(posts.getTitle()).isEqualTo(title);
        assertThat(posts.getContent()).isEqualTo(content);
    }

    @Test
    public void BaseTimeEntity_등록(){

        LocalDateTime now = LocalDateTime.of(2019,6,4,0,0,0);
        postsRepository.save(Posts.builder()
        .title("title")
                .content("content")
                .author("author")
                .build());


        List<Posts> postsList = postsRepository.findAll();


        Posts posts = postsList.get(0);

        System.out.println(">>>>>>>>>>>> createDate="+posts.getCreatedDate()+", modifiedDate ="+posts.getModifiedDate());

        assertThat(posts.getCreatedDate()).isAfter(now);
        assertThat(posts.getModifiedDate()).isAfter(now);
    }
}
