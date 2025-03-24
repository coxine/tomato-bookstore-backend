package tg.cos.tomatomall.util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tg.cos.tomatomall.po.Account;
import tg.cos.tomatomall.repository.AccountRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import java.util.Date;
@Component
public class TokenUtil {
    private static final long EXPIRE_TIME = 24 * 60 * 60 * 1000;

    @Autowired
    AccountRepository accountRepository;

    public String getToken(Account Account) {
        Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        return JWT.create()
                .withAudience(String.valueOf(Account.getId()))
                .withExpiresAt(date)
                .sign(Algorithm.HMAC256(Account.getPassword()));
    }

    public boolean verifyToken(String token) {
        try {
            Integer AccountId=Integer.parseInt(JWT.decode(token).getAudience().get(0));
            Account Account= accountRepository.findById(AccountId).get();
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(Account.getPassword())).build();
            jwtVerifier.verify(token);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public Account getAccount(String token){
        Integer AccountId=Integer.parseInt(JWT.decode(token).getAudience().get(0));
        return accountRepository.findById(AccountId).get();
    }
}
