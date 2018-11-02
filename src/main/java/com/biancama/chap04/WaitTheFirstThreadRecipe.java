package com.biancama.chap04;

import static org.apache.commons.lang3.RandomUtils.nextBoolean;
import static org.apache.commons.lang3.RandomUtils.nextLong;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by massimo.biancalani on 07/08/2017.
 */
public class WaitTheFirstThreadRecipe {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String username="test";
        String password="test";
        UserValidator ldapValidator=new UserValidator("LDAP");
        UserValidator dbValidator=new UserValidator("DataBase");
        ValidatorTask ldapTask=new ValidatorTask(ldapValidator,
            username, password);
        ValidatorTask dbTask=new ValidatorTask(dbValidator,
            username,password);

        List<ValidatorTask> tasks = new ArrayList<>();
        tasks.add(ldapTask);
        tasks.add(dbTask);

        ExecutorService executors = Executors.newCachedThreadPool();

        String result;

        result = executors.invokeAny(tasks);
        System.out.printf("Main: Result: %s\n",result);

        executors.shutdown();
        System.out.printf("Main: End of the Execution\n");
    }
}
@RequiredArgsConstructor
class UserValidator {
    @Getter
    private final String name;


    public boolean validate(String name, String password) {
        try {
            long duration=nextLong(1, 10);
            System.out.printf("Validator %s: Validating a user during %d seconds\n", this.name,duration);
                TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
            return false;
        }
        return nextBoolean();
    }
}
@RequiredArgsConstructor
class ValidatorTask implements Callable<String> {
    private final UserValidator validator;
    private final String user;
    private final String password;

    @Override
    public String call() throws Exception {
        if (!validator.validate(user, password)) {
            System.out.printf("%s: The user has not been found\n",
                validator.getName());
            throw new Exception("Error validating user");
        }
        System.out.printf("%s: The user has been found\n",
            validator.getName());
        return validator.getName();

    }
}