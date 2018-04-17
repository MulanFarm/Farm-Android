package com.daweichang.vcfarm;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        System.out.print(getCreateDate());
    }

    public String create_date = "2017-03-13 21:38:56";
    private transient String createDate;

    public String getCreateDate() {
        createDate = create_date.split(" ")[0];
        return createDate;
    }
}