package org.wallet.walletserver.services.jpa;

import org.wallet.walletdata.model.User;

public interface UserService {

    User getUser(Long id);
}
