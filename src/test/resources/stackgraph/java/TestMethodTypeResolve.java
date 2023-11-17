// s0
public class TestMethodTypeResolve {

  // s16, def_fun(39), def_a(40)
  public void fun(int a) {}

  // s17, def_fun(41), def_a(42)
  public void fun(boolean a) {}

  // s18, def_fun(43), def_a(44)
  public void fun(String a) {}

  // s19, def_fun(45), def_a(46)
  public void fun(RandomClass a) {}

  // s1, def_intResolve(20)
  public void intResolve() {
    int a = 1; // s2, def_a(21)
    fun(1); // s3, use_fun(22)
    fun(a); // s4, use_fun(23), use_a(24)
  }

  // s5, def_boolResolve(25)
  public void booleanResolve() {
    boolean a = true; // s6, def_a(26)
    fun(true); // s7, use_fun(27)
    fun(a); // s8, use_fun(28), use_a(29)
  }

  // s9, def_stringResolve(30)
  public void stringResolve() {
    String a = "1"; // s10, def_a(31)
    fun("1"); // s11, use_fun(32)
    fun(a); // s12, use_fun(33), use_a(34)
  }

  // s13, classResolve(35)
  public void classResolve() {
    RandomClass a = null; // s14, def_a(36)
    fun(a); // s15, use_fun(37), use_a(38)
  }

}
