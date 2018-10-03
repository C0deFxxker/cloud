package com.lyl.study.cloud.vip.core.test;

import com.lyl.study.cloud.vip.core.CoreApplication;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CoreApplication.class)
public class BaseTest extends Assert {
}
