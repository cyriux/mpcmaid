package com.mpcmaid.gui;

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.mpcmaid.pgm.Element;
import com.mpcmaid.pgm.Parameter;
import com.mpcmaid.pgm.Range;
import com.mpcmaid.pgm.Parameter.TuningType;
import com.mpcmaid.pgm.Parameter.EnumType;
import com.mpcmaid.pgm.Parameter.IntType;
import com.mpcmaid.pgm.Parameter.TextType;
import com.mpcmaid.pgm.Parameter.OffIntType;
import com.mpcmaid.pgm.Parameter.RangeType;

/**
 * @pattern Composite target=BindingCapable
 * @pattern Composite target=JComponent
 * 
 * @author cyrille martraire
 */
public class WidgetPanel extends JPanel implements BindingCapable {

	private static final long serialVersionUID = -8139985486559953248L;

	private final Element element;

	public WidgetPanel(Element element) {
		this.element = element;
	}

	public void make() {
		setLayout(new GridLayout(12, 1, 10, 5));
		add(new JLabel(""));

		makeParameters();
	}

	protected void makeParameters() {
		final Parameter[] parameters = element.getParameters();
		for (int j = 0; j < parameters.length; j++) {
			final Parameter parameter = parameters[j];
			makeParameter(parameter);
		}
	}

	protected void makeParameter(final Parameter parameter) {
		if (parameter.getType() instanceof RangeType) {
			makeIntegerParameter(parameter);
		} else if (parameter.getType() instanceof EnumType) {
			makeEnumParameter(parameter);
		} else if (parameter.getType() instanceof OffIntType) {
			makeOffIntParameter(parameter);
		} else if (parameter.getType() instanceof IntType) {
			makeIntParameter(parameter);
		} else if (parameter.getType() instanceof TuningType) {
			makeDecimalParameter(parameter);
		} else if (parameter.getType() instanceof TextType) {
			makeLabelParameter(parameter);
		}
	}

	protected void addWidget(final Widget<?> widget) {
		add(widget);
	}

	public Element getElement() {
		return element;
	}

	protected void makeLabelParameter(final Parameter parameter) {
		final Widget.StringField widget = new Widget.StringField(element, parameter);
		addWidget(widget);
	}

	protected void makeIntParameter(final Parameter parameter) {
		final Widget.IntegerField widget = new Widget.IntegerField(element, parameter);
		addWidget(widget);
	}

	protected void makeEnumParameter(final Parameter parameter) {
		final Widget.ComboField widget = new Widget.ComboField(element, parameter);
		addWidget(widget);
	}

	protected void makeIntegerParameter(final Parameter parameter) {
		final Widget.RangeField widget = new Widget.RangeField(element, parameter);
		addWidget(widget);
	}

	protected void makeOffIntParameter(final Parameter parameter) {
		final String[] values = enumerate(parameter, "");
		final Widget.OffIntegerField widget = new Widget.OffIntegerField(element, parameter, values);
		addWidget(widget);
	}

	public void makeDecimalParameter(Parameter parameter) {
		throw new UnsupportedOperationException(element + ": " + parameter);
	}

	public void load() {
		final Component[] widgets = getComponents();
		for (int i = 0; i < widgets.length; i++) {
			if (widgets[i] instanceof BindingCapable) {
				BindingCapable component = (BindingCapable) widgets[i];
				try {
					component.load();
				} catch (Exception e) {
					System.out.println("xception: " + component + " " + i);
					e.printStackTrace();
				}
			}
		}
	}

	public void save() {
		final Component[] widgets = getComponents();
		for (int i = 0; i < widgets.length; i++) {
			WidgetPanel component = (WidgetPanel) widgets[i];
			component.save();
		}
	}

	public final static String[] enumerate(Parameter parameter, String prefix) {
		final OffIntType type = (OffIntType) parameter.getType();
		final Range range = type.getRange();
		final String[] values = new String[range.getHigh() - range.getLow() + 1];
		for (int i = 0; i < values.length; i++) {
			values[i] = i == 0 ? "Off" : prefix + i;
		}
		return values;
	}

	public String toString() {
		return "WidgetPanel for element: " + getElement();
	}

}