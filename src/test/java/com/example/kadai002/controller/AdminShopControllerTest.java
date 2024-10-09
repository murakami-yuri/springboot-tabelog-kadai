package com.example.kadai002.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AdminShopControllerTest {
	@Autowired
    private MockMvc mockMvc;
	
	@Test
    public void 未ログインの場合は管理者用の店舗一覧ページからログインページにリダイレクトする() throws Exception {
        mockMvc.perform(get("/admin/shops"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithUserDetails("taro.samurai@example.com")
    public void 一般ユーザーとしてログイン済みの場合は管理者用の民宿一覧ページが表示されずに403エラーが発生する() throws Exception {
        mockMvc.perform(get("/admin/shops"))
               .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("hanako.samurai@example.com")
    public void 管理者としてログイン済みの場合は管理者用の民宿一覧ページが正しく表示される() throws Exception {
        mockMvc.perform(get("/admin/shops"))
               .andExpect(status().isOk())
               .andExpect(view().name("admin/shops/index"));
    }
}
