
    class PTransistorElm extends TransistorElm {
	public PTransistorElm(int xx, int yy) { super(xx, yy, true); }
	@Override
	Class getDumpClass() { return TransistorElm.class; }
    }
