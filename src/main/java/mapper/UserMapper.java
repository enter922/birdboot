package mapper;

import entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
    List<User> selectAll();

    boolean  addUser(User user);

    User checkUser(@Param("username") String name, @Param("password")String password);
}
