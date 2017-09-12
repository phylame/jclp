import jclp.text.ConverterManager;

import java.util.Date;

public class Test {
    public static void main(String[] args) throws Exception {
        System.out.println(ConverterManager.render(new Date()));
    }
}
