Each circuit definition file contains at least one row which describes parameters of electric element with flag field.
Usually such elements as wire, resistor and voltage input don't use flag field. In this application we are going to use
flag field to define wether element serves as an input, control or output terminal.

For example wire is defined as follows:
w 304 336 384 320 0

Note the first letter denotes type of the element (w=wire) and next 4 digits denote it's location in the circuit.

While the last number which is set to 0 by default is a dedicated flag.

We will use flag filed as follows:
        0 - Not a part of control elements
  1 -  99 - Input channels (wire=w) (note! we can't use 0 flag since multiple wires are used in the circuit)
199 - 199 - Control voltages (voltage source = R )
200 - 299 - Output channels (wire=w)
