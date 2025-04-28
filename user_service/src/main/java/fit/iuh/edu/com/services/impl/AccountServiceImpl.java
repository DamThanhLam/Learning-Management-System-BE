package fit.iuh.edu.com.services.impl;

import fit.iuh.edu.com.models.Account;
import fit.iuh.edu.com.models.User;
import fit.iuh.edu.com.repositories.AccountRepository;
import fit.iuh.edu.com.repositories.UserRepository;
import fit.iuh.edu.com.services.bl.AccountServiceBL;
import fit.iuh.edu.com.utils.JwtTokenUtil;
import fit.iuh.edu.com.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountServiceBL {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtEncoder jwtEncoder;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private SecurityUtils securityUtils;



    @Autowired
    private UserRepository userRepository;

    @Override
    public void addAccount(Account account) {
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        accountRepository.create(account);
    }

    @Override
    public boolean beforeRegister(String email) {
        return accountRepository.accountExists(email) == null;
    }

    @Override
    public String login(String email, String password) {
        Account account = accountRepository.accountExists(email);
        System.out.println("account"+password);
        System.out.println("email"+email);
        if (account == null) return null;
        if(passwordEncoder.matches(password,account.getPassword())){
            User user = userRepository.find(account.getId());
            SecurityUtils.setUserAuthentication(user);


            System.out.println("user"+user);

            return jwtTokenUtil.generateToken(jwtEncoder,user);
        }
        return null;
    }

    @Override
    public Account getAccount(String email) {
        return accountRepository.getByEmail(email);
    }


}
