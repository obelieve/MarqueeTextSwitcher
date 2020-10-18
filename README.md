# MarqueeTextSwitcher
公告栏 文本翻页显示 + 跑马灯滚动效果

![image](https://thumbsnap.com/i/bfQkkmjg.gif)


### Step 1. Add the JitPack repository to your build file
```
...
allprojects {
    repositories {
        ...
        maven(){url 'https://jitpack.io'}
    }
}
...
```
### Step 2. Add the dependency
```
	dependencies {
	        implementation 'com.github.obelieve:MarqueeTextSwitcher:1.0.0'
	}
```

### Step 3. Use
```xml
    <com.obelieve.marqueetextswitcher.MarqueeTextSwitcher
        android:id="@+id/mts_content"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>
```
```java
        //视图不可见时，自动暂停，无需自行控制。
        List<String> list = Arrays.asList("");
        mtsContent.setTextList(list);
        mtsContent.startRun();
```
### 具体参数
```xml
app:textDuration="3000" //文本显示 花费时间/ms  默认 3000
app:animDuration="1000" //文本切换 花费时间/ms  默认 1000
app:marqueeDelayedDuration ="3000" // 文本滚动时 延迟等待时间/ms  默认 3000
app:textSize="14sp" //文本尺寸 默认14sp
app:textColor="#333333" //文本颜色 默认 #333333
app:stepSpeed="1.5f" //滚动速度 默认1.5f
```

### 参考：
- 1. HorizontalScrollTextView
- 2. PagingTextSwitcher
