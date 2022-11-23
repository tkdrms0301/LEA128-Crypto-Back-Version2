package kumoh.util.crypto.symm;

import kumoh.util.crypto.BlockCipher;
import kumoh.util.crypto.engine.LeaEngine;
import kumoh.util.crypto.mode.CBCMode;
import kumoh.util.crypto.mode.CTRMode;

public class LEA {
	private LEA() {
		throw new AssertionError();
	}

	public static final BlockCipher getEngine() {
		return new LeaEngine();
	}

	public static final class CBC extends CBCMode {
		public CBC() {
			super(getEngine());
		}
	}

	public static final class CTR extends CTRMode {
		public CTR() {
			super(getEngine());
		}
	}

}
