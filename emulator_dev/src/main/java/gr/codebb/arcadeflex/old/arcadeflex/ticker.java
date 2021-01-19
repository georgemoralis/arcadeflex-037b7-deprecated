package gr.codebb.arcadeflex.old.arcadeflex;

import static gr.codebb.arcadeflex.old.arcadeflex.libc_old.*;

public class ticker {
  static FILE errorlog=null;
  public static long TICKS_PER_SEC; 
  
  public static long ticker()
  {
      return uclock();
  }
  public static void init_ticker()
  {
    TICKS_PER_SEC = UCLOCKS_PER_SEC;

    if (errorlog!=null) fprintf(errorlog,"using uclock() for timing\n");
  }
  
}
