
    class ACVoltageElm extends VoltageElm {
	public ACVoltageElm(int xx, int yy) { super(xx, yy, WF_AC); }
	@Override
	Class getDumpClass() { return VoltageElm.class; }
    }
