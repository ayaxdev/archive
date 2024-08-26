package net.jezevcik.argon.account.repository;

import net.jezevcik.argon.account.Account;
import net.jezevcik.argon.repository.ElementRepository;
import net.jezevcik.argon.repository.params.RepositoryParams;

public class AccountRepository extends ElementRepository<Account> {

    public AccountRepository() {
        super("Accounts", RepositoryParams.<Account>builder().build());
    }

}
