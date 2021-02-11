package projekti.Repositories;

import projekti.Entities.Account;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByFirstNameIgnoreCaseContainingOrLastNameIgnoreCaseContaining(String firstName, String lastName);
    Account findByEmailAddress(String emailAddress);
    Account findByUsername(String username);
    Account findByEmailAddressAndPassword(String emailAddress, String password);

    @Modifying
    @Query(value = "UPDATE ACCOUNT a SET a.username = ? WHERE a.username = ?", nativeQuery = true) // I wish I could parameterize a column with a.? 
    void updateUsername(
            @Param("value") String value,
            @Param("username") String username);

    @Modifying
    @Query(value = "UPDATE ACCOUNT a SET a.biography = ? WHERE a.username = ?", nativeQuery = true)
    void updateBio(
            @Param("value") String value,
            @Param("username") String username);

    @Modifying
    @Query(value = "UPDATE ACCOUNT a SET a.personal_website_address = ? WHERE a.username = ?", nativeQuery = true)
    void updateWebsite(
            @Param("value") String value,
            @Param("username") String username);

    @Modifying
    @Query(value = "UPDATE ACCOUNT a SET a.online = ? WHERE a.username = ?", nativeQuery = true)
    void updateOnline(
            @Param("value") boolean value,
            @Param("username") String username);

    @Modifying
    @Query(value = "UPDATE ACCOUNT a SET a.profile_image = ? WHERE a.username = ?", nativeQuery = true)
    void saveProfilePicture(
            @Param("value") byte[] image,
            @Param("username") String username);
}
