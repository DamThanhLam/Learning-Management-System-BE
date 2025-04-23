package fit.iuh.edu.com.services.BL;

import fit.iuh.edu.com.models.Account;
import org.springframework.stereotype.Service;

@Service
public interface AccountServiceBL {
    public void addAccount(Account account);
    public boolean beforeRegister(String email);

    String login(String email, String string);
    Account getAccount(String email);
}

