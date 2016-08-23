
    class NJfetElm extends JfetElm {
	public NJfetElm(int xx, int yy) { super(xx, yy, false); }
	@Override
	Class getDumpClass() { return JfetElm.class; }
    }

    class PJfetElm extends JfetElm {
	public PJfetElm(int xx, int yy) { super(xx, yy, true); }
	@Override
	Class getDumpClass() { return JfetElm.class; }
    }

