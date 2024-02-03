package com.fortress.service;

import com.fortress.entity.User;
import com.fortress.dto.UserDTO;
import com.fortress.errorhandler.FortressBeacon;
import com.fortress.repo.UserRepo;
import com.fortress.security.JwtConfig;
import com.fortress.utilities.PasswordValidator;
import com.fortress.utilities.SecureCodeGenerator;
import com.fortress.utilities.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Deprecated
public class UserService extends CRUDService<User, UserDTO> implements UserDetailsService {

    private final UserRepo repo;
    private final PasswordEncoder passwordEncoder;
    private final EmailSender emailSender;

    private final Validator emailValidator;

    private final Validator passwordValidator;
    @Value("${password.length}")
    private int passwordLength;

    private final JwtConfig jwtConfig;

    public UserService(UserRepo repo, PasswordEncoder passwordEncoder,
                       EmailSender emailSender, Validator emailValidator,
                       Validator passwordValidator, JwtConfig jwtConfig) {
        super(repo);
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
        this.emailSender = emailSender;
        this.emailValidator = emailValidator;
        this.passwordValidator = passwordValidator;
        this.jwtConfig = jwtConfig;
    }

    @Override
    public boolean beforeAdd(UserDTO userDTO) {
        validateUsername(userDTO.getUsername());
        return true;
    }

    @Override
    public void afterAdd(UserDTO userDTO) {
        emailSecurePassword(userDTO);
    }

    public void emailSecurePassword(UserDTO userDTO){
        var user = findById(userDTO.getId());

        var tempPassword = getSecureCode();
        user.setPassword(tempPassword);
        encodePassword(user);

        repo.save(user);

        emailSecureCode(tempPassword, user.getUsername(),
                "Temp Password");
    }

    private void processPassword(User user){
        validatePassword(user);
        encodePassword(user);
    }

    private void validateUsername(String username){

        if (!emailValidator.validate(username))
            throw new FortressBeacon("Username not valid");

        if(usernameAlreadyExists(username))
            throw new FortressBeacon("User already exist");
    }

    private void validatePassword(User user){
        var password = user.getPassword();

        if(!passwordValidator.validate(password))
            throw new FortressBeacon("Password not valid " +
                    PasswordValidator.passwordPolicy);
    }

    private void encodePassword(User user){
        var password = user.getPassword();

        var hashedPassword = passwordEncoder.encode(password);
        user.setPassword(hashedPassword);
    }

    private boolean usernameAlreadyExists(String email) {
        var user = findByUsername(email);
        return user!=null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = findByUsername(username);
        if (user!=null)
            return user;

        throw new UsernameNotFoundException("Username not found");
    }

    private User findByUsername(String username){
        Optional<User> user =repo.findByUsername(username);
        return user.orElse(null);
    }

    public UserDTO findDTOByUsername(String username){
        var user = findByUsername(username);

        if(user!=null)
            return mapToDTO(user);

        return null;
    }

    public List<UserDTO> findUsersStartsWith(String usernamePrefix){
        if(usernamePrefix==null)
            return super.getAll();

        var results = repo.findByUsernameStartsWith(usernamePrefix.trim().toLowerCase());

        return mapToDTO(results);
    }

    @Override
    public void updateEntity(UserDTO dto, User currentEntity) {
        updateUsername(dto, currentEntity);
        currentEntity.setName(dto.getName());
        currentEntity.setRole(dto.getRole());
    }

    private void updateUsername(UserDTO dto, User currentEntity){
        var updatedUsername = dto.getUsername();
        var currentUsername = currentEntity.getUsername();

        if(!updatedUsername.equals(currentUsername)) {
            validateUsername(updatedUsername);
            currentEntity.setUsername(updatedUsername);
        }
    }

    private User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }

    public void resetPasswordWithToken(UserDTO userDTO){

        var claimedResetCode = userDTO.getPasswordResetCode();

        if(jwtConfig.tokenValid(claimedResetCode)){

            var userName = jwtConfig.getSubject(claimedResetCode);
            var user = (User) loadUserByUsername(userName);

            changePasswordWithoutOldPassword(userDTO, user);
        }
    }

    public void sendPasswordResetToken(UserDTO userDTO){
        var user = (User) loadUserByUsername(userDTO.getUsername());
        var username = user.getUsername();
        var passwordRestCode = jwtConfig
                .generateAccessToken(username)
                .getAccessToken();

        emailSecureCode(passwordRestCode, user.getUsername(),
                "Password Reset Code");
    }

    private void emailSecureCode(String secureCode, String username, String subject){
        emailSender.send(username, subject, secureCode);
    }

    private String getSecureCode(){
        return SecureCodeGenerator.generate(passwordLength);
    }

    public void changePassword(UserDTO userDTO){
        var user = getCurrentAuthenticatedUser();
        assert user != null;
        changePassword(userDTO, user);
    }

    private void changePassword(UserDTO userDTO, User user){
        if(oldPasswordValid(userDTO.getOldPassword(), user.getPassword())) {
            changePasswordWithoutOldPassword(userDTO, user);
        }else {
            throw new FortressBeacon("The old password provided is incorrect");
        }
    }

    private void changePasswordWithoutOldPassword(UserDTO userDTO, User user){
        user.setPassword(userDTO.getPassword());
        processPassword(user);
        repo.save(user);
    }

    public boolean oldPasswordValid(String oldPassword, String currentPassword){
        return passwordEncoder.matches(oldPassword, currentPassword);
    }

    public void toggleLock(UserDTO userDTO){
        var userToToggle = findById(userDTO.getId());
        userToToggle.setAccountNonLocked(!userToToggle.isAccountNonLocked());
        repo.save(userToToggle);
    }

    public void changeName(UserDTO user){
        var currentAuthenticatedUser = getCurrentAuthenticatedUser();
        assert currentAuthenticatedUser != null;
        currentAuthenticatedUser.setName(user.getName());
        repo.save(currentAuthenticatedUser);
    }

    public UserDTO getNameOfAuthenticatedUser(){
        var currentAuthenticatedUser = getCurrentAuthenticatedUser();
        var userDTO = new UserDTO();
        assert currentAuthenticatedUser != null;
        userDTO.setName(currentAuthenticatedUser.getName());

        return userDTO;
    }
}
