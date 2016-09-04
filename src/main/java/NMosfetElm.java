
    class NMosfetElm extends MosfetElm {
	public NMosfetElm(int xx, int yy) { super(xx, yy, false); }
	@Override
	Class getDumpClass() { return MosfetElm.class; }
    }
