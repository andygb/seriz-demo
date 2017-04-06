package com.seried.laurel;

import com.seried.dto.Person;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import java.util.ArrayList;
import java.util.Date;
import redis.clients.jedis.Jedis;

/**
 * Created by rick on 2017/4/5.
 */
public class ProtoStuffTest {


  public static void main(String[] args) throws Exception{

    /**
     *                             _ooOoo_
     *                            o8888888o
     *                            88" . "88
     *                            (| -_- |)
     *                            O\  =  /O
     *                         ____/`---'\____
     *                       .'  \\|     |//  `.
     *                      /  \\|||  :  |||//  \
     *                     /  _||||| -:- |||||-  \
     *                     |   | \\\  -  /// |   |
     *                     | \_|  ''\---/''  |   |
     *                     \  .-\__  `-`  ___/-. /
     *                   ___`. .'  /--.--\  `. . __
     *                ."" '<  `.___\_<|>_/___.'  >'"".
     *               | | :  `- \`.;`\ _ /`;.`/ - ` : | |
     *               \  \ `-.   \_ __\ /__ _/   .-` /  /
     *          ======`-.____`-.___\_____/___.-`____.-'======
     *                             `=---='
     *          ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
     *                     佛祖保佑        永无BUG
     *            佛曰:
     *                   写字楼里写字间，写字间里程序员；
     *                   程序人员写程序，又拿程序换酒钱。
     *                   酒醒只在网上坐，酒醉还来网下眠；
     *                   酒醉酒醒日复日，网上网下年复年。
     *                   但愿老死电脑间，不愿鞠躬老板前；
     *                   奔驰宝马贵者趣，公交自行程序员。
     *                   别人笑我忒疯癫，我笑自己命太贱；
     *                   不见满街漂亮妹，哪个归得程序员？
     */

    LinkedBuffer linkedBuffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
    Schema<Person> schema = RuntimeSchema.createFrom(Person.class);

    Person person = new Person(1, "Tom", 28, new Date(), new ArrayList<Person>());
    Person meta1 = new Person(11, "小红", 26, new Date(), null);
    Person meta2 = new Person(12, "小明", 22, new Date(), null);
    Person meta3 = new Person(13, "小刚", 24, new Date(), null);

    person.getClassMetas().add(meta1);
    person.getClassMetas().add(meta2);
    person.getClassMetas().add(meta3);

    byte[] data = ProtostuffIOUtil.toByteArray(person, schema, linkedBuffer);

    Jedis redis = new Jedis("127.0.0.1", 6379);//连接redis

    byte[] keyByte = "testPerson".getBytes("UTF-8");

    redis.set(keyByte, data);

    byte[] dataByte = redis.get(keyByte);

    if (dataByte != null) {
      Person schemaPerson = schema.newMessage();
      ProtostuffIOUtil.mergeFrom(dataByte, schemaPerson, schema);

      if (schemaPerson != null) {
        System.out.println(schemaPerson.toString());
      }
    }

  }

}
