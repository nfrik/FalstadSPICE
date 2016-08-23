
    class PushSwitchElm extends SwitchElm {
	public PushSwitchElm(int xx, int yy) { super(xx, yy, true); }
	@Override
	Class getDumpClass() { return SwitchElm.class; }
    }
