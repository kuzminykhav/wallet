package org.wallet.walletdata.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.wallet.walletdata.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
}
