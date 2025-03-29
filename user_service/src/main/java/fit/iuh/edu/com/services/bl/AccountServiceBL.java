package fit.iuh.edu.com.services.bl;

import fit.iuh.edu.com.models.Account;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

@Service
public interface AccountServiceBL {
    public void addAccount(Account account);
    public boolean beforeRegister(String email);

    String login(String email, String string);
}

