package gr.codebb.arcadeflex.v037b7.common;

import static gr.codebb.arcadeflex.common.PtrLib.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.*;
import static gr.codebb.arcadeflex.v037b7.mame.sndintrfH.*;

public class fucPtr {

  public abstract static interface ReadHandlerPtr {

    public abstract int handler(int offset);
  }

  public abstract static interface WriteHandlerPtr {

    public abstract void handler(int offset, int data);
  }

  public abstract static interface InitMachinePtr {

    public abstract void handler();
  }

  public abstract static interface InitDriverPtr {

    public abstract void handler();
  }

  public abstract static interface InterruptPtr {

    public abstract int handler();
  }

  public abstract static interface VhConvertColorPromPtr {

    public abstract void handler(char[] palette, char[] colortable, UBytePtr color_prom);
  }

  public abstract static interface VhEofCallbackPtr {

    public abstract void handler();
  }

  public abstract static interface VhStartPtr {

    public abstract int handler();
  }

  public abstract static interface VhStopPtr {

    public abstract void handler();
  }

  public abstract static interface VhUpdatePtr {

    public abstract void handler(osd_bitmap bitmap, int full_refresh);
  }

  public abstract static interface ShStartPtr {

    public abstract int handler(MachineSound msound);
  }

  public abstract static interface ShStopPtr {

    public abstract void handler();
  }

  public abstract static interface ShUpdatePtr {

    public abstract void handler();
  }

  public abstract static interface RomLoadPtr {

    public abstract void handler();
  }

  public abstract static interface InputPortPtr {

    public abstract void handler();
  }

  public abstract static interface nvramPtr {

    public abstract void handler(Object file, int read_or_write);
  }
  ;

  public abstract static interface WriteYmHandlerPtr {

    public abstract void handler(int linestate);
  }
}
