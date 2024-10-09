package com.example.kadai002.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.kadai002.entity.Role;
import com.example.kadai002.entity.User;
import com.example.kadai002.form.AuthUpdateForm;
import com.example.kadai002.form.NicknameUpdateForm;
import com.example.kadai002.form.SignupForm;
import com.example.kadai002.form.UserEditForm;
import com.example.kadai002.repository.RoleRepository;
import com.example.kadai002.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {
	private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    // すべてのユーザーを、ページングされた状態で取得する
    public Page<User> findAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
    // 指定したidを持つユーザーを取得する
    public Optional<User> findUserById(Integer id) {
        return userRepository.findById(id);
    }
    
    // 指定したメールアドレスを持つユーザーを取得する
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    // 指定されたkeywordをメールに含むユーザーを、ページングされた状態で取得する
    public Page<User> findUsersByEmailLike(String keyword, Pageable pageable) {
        return userRepository.findByEmailLike("%" + keyword + "%", pageable);
    }
    
    // ユーザーの登録
    @Transactional
    public User createUser(SignupForm signupForm, String roleName) {
        User user = new User();
        //Role role = roleRepository.findByName("ROLE_GENERAL");
        Role role = roleRepository.findByName(roleName);

        user.setNickname(signupForm.getNickname());
        user.setEmail(signupForm.getEmail());
        user.setPassword(passwordEncoder.encode(signupForm.getPassword()));
        user.setRole(role);
        user.setEnabled(false);
        user.setEligible(false);

        return userRepository.save(user);
    }
    
    // ユーザー情報の更新
    @Transactional
    public User updateUser(AuthUpdateForm authUpdateForm) {

        User user = findUserByEmail(authUpdateForm.getEmail());
        user.setPassword(passwordEncoder.encode(authUpdateForm.getPassword()));
        user.setEnabled(false);

        return userRepository.save(user);
    }
    
    // 管理ユーザーによる強制的な会員情報の編集
    @Transactional
    public User updateUserByAdmin(UserEditForm userEditForm, User user) {
    	
    	// フォーム入力で空でない項目は更新
        if (!userEditForm.getEmail().isEmpty()) {
        	user.setEmail(userEditForm.getEmail());
        }
        if (!userEditForm.getPassword().isEmpty()) {
        	user.setPassword(passwordEncoder.encode(userEditForm.getPassword()));
        }
        if (!userEditForm.getNickname().isEmpty()) {
        	userEditForm.setNickname(userEditForm.getNickname());
        }

        return userRepository.save(user);
    }
    
 // メールアドレスの変更
    @Transactional
    public User updateEmail(AuthUpdateForm authUpdateForm, User user) {

    	String email = authUpdateForm.getEmail();
    	user.setEmail(email);
        user.setPassword(passwordEncoder.encode(authUpdateForm.getPassword()));
        user.setEnabled(false);

        return userRepository.save(user);
    }
    
    // ニックネームの変更（認証なし）
    @Transactional
    public User updateNickname(NicknameUpdateForm nicknameUpdateForm) {

        User user = findUserByEmail(nicknameUpdateForm.getEmail());
        user.setNickname(nicknameUpdateForm.getNickname());

        return userRepository.save(user);
    }
     
    // ユーザーを有効にする
    @Transactional
    public void enableUser(User user) {
        user.setEnabled(true);
        userRepository.save(user);
    }
    
    //　ユーザーを削除する
    @Transactional
    public void deleteUser(User user) {
        userRepository.delete(user);
    }
    
    // 有料会員にアップグレードする際のユーザーテーブルの書き換え
    @Transactional
    public void upgradeUser(Integer userId) {
    	
    	//nteger userId = Integer.valueOf(sessionMetadata.get("userId"));
    	// これまでとは別の書き方！
    	Optional<User> optionalUser = userRepository.findById(userId);
        User user = optionalUser.orElseThrow(() -> new EntityNotFoundException("指定されたIDのユーザーが存在しません。")); 
    	
        System.out.println("-----------------------Upgraded User: " + user.getNickname());
        
    	user.setEligible(true);
    	
    	userRepository.save(user);
    }
    
    // 無料会員にダウングレードする際のユーザーテーブルの書き換え
    @Transactional
    public void downgradeUser(Integer userId) {
    	
    	//nteger userId = Integer.valueOf(sessionMetadata.get("userId"));
    	// これまでとは別の書き方！
    	Optional<User> optionalUser = userRepository.findById(userId);
        User user = optionalUser.orElseThrow(() -> new EntityNotFoundException("指定されたIDのユーザーが存在しません。")); 
        
    	user.setEligible(false);
    	
    	userRepository.save(user);
    }
    
    // メールアドレスが登録済みかどうかをチェックする
    public boolean isEmailRegistered(String email) {
        User user = userRepository.findByEmail(email);
        return user != null;
    }
    
    // パスワードとパスワード（確認用）の入力値が一致するかどうかをチェックする
    public boolean isSamePassword(String password, String passwordConfirmation) {
        return password.equals(passwordConfirmation);
    }
}
