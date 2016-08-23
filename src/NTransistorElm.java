
    class NTransistorElm extends TransistorElm {
	public NTransistorElm(int xx, int yy) { super(xx, yy, false); }
	@Override
	Class getDumpClass() { return TransistorElm.class; }
    }
