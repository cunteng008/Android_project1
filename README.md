#daygram
一款日记应用
===========================
本app是模仿应用商城上的Daygram用自己的方式实现的
****
###　　　　　　　　　　　　Author:cunteng008
###　　　　　　　　　 E-mail:mingjingc3721@gmail.com
  
===========================
<h3>关键技术点</h3>
<pre><code>1.sitview显示多种布局，需要重写baseAdapter, 但要注意这里一定要修改 public int getViewTypeCount()，以确定最多使用的布局种数，否则程序会在运行时崩溃

2.startActivityForResult(mIntent,1)这里的1是本次操作的代号，可以设任意数，但要在onActivityResult()对相应的操作代号的操作进行处理
3.文件的存取为了简便，将每月的数据存到了一个独立的文件，当读取文件的函数返回null时则视为没有任何数据，需用空字符串为初始化。在用listview显示是，字符串长度为0则显示点。
4.在阅览日记时mViewImageViewSwitch开关打开，表示正在处于阅览状态。可对阅览的日记做相应的操作，再按一次阅览的按钮时回到原界面,若在阅览状态下对日记进行修改，修改完成后要重新调用函数刷新阅览的内容，总的内容也要更新，一确保在返回原界面时显示的数据同步
5.Data类存的是日记的数据，除了内容外全为整数，新建一个类放星期和月份的字符串常量，以便调用。
6.刷新数据后再显示时为了保证listtem的位置保持不变，可以调用notifyDataSetChanged()，而不必调用setAdapter重新设置一次。
7.注意get(Calendar.MONTH)取到的数从0开始
8. 预览日记最多显示两行   android:maxLines="2” android:ellipsize="end"
9. 在非全屏模式下，将activity的windowSoftInputMode的属性设置为：adjustResize。同时在View的onSizeChanged(int w, int h, int oldw, int oldh)里可以得到变化后的尺寸，然后根据前后变化的结果来计算屏幕需要移动的距离。
 <activity
       android:name=".activity.iputTextActivity"
       android:windowSoftInputMode="adjustResize">
 </activity>
但是在全屏模式下，即使将activity的windowSoftInputMode的属性设置为：adjustResize。在键盘显示时它未将Activity的Screen向上推动，所以你Activity的view的根树的尺寸是没有变化的。在这种情况下，你也就无法得知键盘的尺寸，对根view的作相应的推移。github上已有封装好了的方法 AndroidBug5497Workaround()，我们使用时直接将语句AndroidBug5497Workaround.assistActivity(this);放在setContentView();之后即可10. RadioGroup加RadioButton可以实现原型单选框。可以实现自己的style="@style/RadioButtonStyle"
11. android:visibility="gone"的利用可以实现一个布局内多种子布局的相互切换，本应用的首页底部控件正是如此实现。
12.密码的输入其实就是利用textedit的监听addTextChangedListener()和textview、11点钟的布局切换、timer倒计时等实现的。实现的方法是每输入一个数就将一个圆形的textview的背景染黑，删除就染成默认染色，输入完后数据若没出错，则延迟显示0.3s

CountDownTimer(300,300)
出现错误或重置密码时前后不一则显示红色警告0.8s,在布局中限制textedit输入的数输入据类型为数字和长度为4,
13.超过下拉利用了刷新的方法显现headview

</code></pre>

<h3>待解决问题</h3>
<pre><code>1.数据存储问题。
目前的用的是文件方式存储所有数据(设置信息和日记数据)，而且是一个月单独存在一个文件，切换月份是都要加载数据，每当改动数据中的某个类的变量，如添加或删除某些类变量时，文件上的数据读取会出错。导致数据的不稳定性。
2.listview虽然用刷新的办法加上的headview(此应用用来显示动态时间),但体验不够流畅，感觉阻力太大，与应用商城上的Daygram相比，自己的仍很差。虽然android有个实现很相似的办法@Override
protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent)
{
		return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, mMaxYOverscrollDistance, isTouchEvent);
}
但添加headview时就不能实现相同的效果。
3.日记预览方框的线条的背景还不能做到透明，这样想要切换主题时实现的效果很差。
4. startActivityForResult因为在设置输入密码的相关activity时，finish后还有无法销毁acitvity，而不得不放弃用另一种方法。
5.底部的控件切换实现过于繁琐，容易漏掉设置某些内容二出错。特别是底部的年份控件设置，其实是利用了六个textview的点击事件加上一些自定义函数实习单选效果，但繁琐度过大，自己调试完后那方面还后续还做了两次小问题修复。
……

</code></pre>
