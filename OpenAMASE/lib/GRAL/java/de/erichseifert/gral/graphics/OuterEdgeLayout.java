// ===============================================================================
// Authors: AFRL/RQQD
// Organization: Air Force Research Laboratory, Aerospace Systems Directorate, Power and Control Division
// 
// Copyright (c) 2017 Government of the United State of America, as represented by
// the Secretary of the Air Force.  No copyright is claimed in the United States under
// Title 17, U.S. Code.  All Other Rights Reserved.
// ===============================================================================

/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2013 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <mseifert[at]error-reports.org>
 *
 * This file is part of GRAL.
 *
 * GRAL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.erichseifert.gral.graphics;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import de.erichseifert.gral.util.Insets2D;
import de.erichseifert.gral.util.Location;


/**
 * Implementation of Layout that arranges a {@link Container}'s components
 * inside or in the regions outside of the container. This is similar to
 * {@link EdgeLayout}, but also allows components to be placed outside the
 * container.
 */
public class OuterEdgeLayout implements Layout {
	/** Version id for serialization. */
	private static final long serialVersionUID = -2238929452967312857L;

	/** Spacing between the container's edges and the components. */
	private final double gap;

	/**
	 * Initializes a layout manager object with the specified space between the
	 * container's edges and the components.
	 * @param gap Spacing between the container's edges and the components.
	 */
	public OuterEdgeLayout(double gap) {
		this.gap = gap;
	}

	/**
	 * Initializes a layout manager object without space between the
	 * components.
	 */
	public OuterEdgeLayout() {
		this(0.0);
	}

	/**
	 * Arranges the components of the specified container according to this
	 * layout.
	 * @param container Container to be laid out.
	 */
	public void layout(Container container) {
		// Fetch components
		Map<Location, Drawable> comps = getComponentsByLocation(container);
		Drawable north = comps.get(Location.NORTH);
		Drawable northEast = comps.get(Location.NORTH_EAST);
		Drawable east = comps.get(Location.EAST);
		Drawable southEast = comps.get(Location.SOUTH_EAST);
		Drawable south = comps.get(Location.SOUTH);
		Drawable southWest = comps.get(Location.SOUTH_WEST);
		Drawable west = comps.get(Location.WEST);
		Drawable northWest = comps.get(Location.NORTH_WEST);
		Drawable center = comps.get(Location.CENTER);

		// Calculate maximum widths and heights
		double widthWest   = getMaxWidth(northWest,  west,   southWest);
		double widthEast   = getMaxWidth(northEast,  east,   southEast);
		double heightNorth = getMaxHeight(northWest, north,  northEast);
		double heightSouth = getMaxHeight(southWest, south,  southEast);

		double gap2 = 2.0*gap;
		double gapEast  = (widthEast > 0.0) ? gap : 0.0;
		double gapWest  = (widthWest > 0.0) ? gap : 0.0;
		double gapNorth = (heightNorth > 0.0) ? gap : 0.0;
		double gapSouth = (heightSouth > 0.0) ? gap : 0.0;

		Rectangle2D bounds = container.getBounds();
		Insets2D insets = container.getInsets();
		if (insets == null) {
			insets = new Insets2D.Double();
		}

		double xWest   = bounds.getMinX() + insets.getLeft() - gapWest - widthWest;
		double xCenter = bounds.getMinX() + insets.getLeft();
		double xEast   = bounds.getMaxX() - insets.getRight() + gapEast;
		double yNorth  = bounds.getMinY() + insets.getTop() - gapNorth - heightNorth;
		double yCenter = bounds.getMinY() + insets.getTop();
		double ySouth  = bounds.getMaxY() - insets.getBottom() + gapSouth;

		layoutComponent(northWest,
			xWest, yNorth,
			widthWest, heightNorth
		);

		layoutComponent(north,
			xCenter, yNorth,
			bounds.getWidth() - insets.getHorizontal(),
			heightNorth
		);

		layoutComponent(northEast,
			xEast, yNorth,
			widthEast, heightNorth
		);

		layoutComponent(east,
			xEast, yCenter,
			widthEast,
			bounds.getHeight() - insets.getVertical()
		);

		layoutComponent(southEast,
			xEast, ySouth,
			widthEast,
			heightSouth
		);

		layoutComponent(south,
			xCenter, ySouth,
			bounds.getWidth() - insets.getHorizontal(),
			heightSouth
		);

		layoutComponent(southWest,
			xWest, ySouth,
			widthWest,
			heightSouth
		);

		layoutComponent(west,
			xWest, yCenter,
			widthWest,
			bounds.getHeight() - insets.getVertical()
		);

		layoutComponent(center,
			xCenter + gap, yCenter + gap,
				bounds.getWidth() - insets.getHorizontal() - gap2,
				bounds.getHeight() - insets.getVertical() - gap2
		);
	}

	/**
	 * Returns the preferred size of the specified container using this layout.
	 * @param container Container whose preferred size is to be returned.
	 * @return Preferred extent of the specified container.
	 */
	public Dimension2D getPreferredSize(Container container) {
		// Fetch components
		Map<Location, Drawable> comps = getComponentsByLocation(container);
		Drawable center = comps.get(Location.CENTER);


		// Calculate preferred dimensions
		Insets2D insets = container.getInsets();
		if (insets == null) {
			insets = new Insets2D.Double();
		}

		double gap2 = 2.0*gap;
		double width = center.getWidth() + insets.getHorizontal() + gap2;
		double height = center.getHeight() + insets.getVertical() + gap2;

		return new de.erichseifert.gral.util.Dimension2D.Double(
			width, height
		);
	}

	/**
	 * Returns the minimal space between components. No space will be allocated
	 * when no components are available.
	 * @return Horizontal and vertical gaps
	 */
	public Dimension2D getGap() {
		Dimension2D gap =
			new de.erichseifert.gral.util.Dimension2D.Double();
		gap.setSize(this.gap, this.gap);
		return gap;
	}

	/**
	 * Returns a map all components which are stored with a {@code Location}
	 * constraint in the specified container.
	 * @param container Container which stores the components
	 * @return A map of all components (values) and their constraints (keys) in
	 *         the specified container.
	 */
	private static Map<Location, Drawable> getComponentsByLocation(Container container) {
		Map<Location, Drawable> drawablesByLocation = new HashMap<Location, Drawable>();
		for (Drawable d: container) {
			Object constraints = container.getConstraints(d);
			if (constraints instanceof Location) {
				drawablesByLocation.put((Location) constraints, d);
			}
		}
		return drawablesByLocation;
	}

	/**
	 * Returns the maximum width of an array of Drawables.
	 * @param drawables Drawables to be measured.
	 * @return Maximum horizontal extent.
	 */
	private static double getMaxWidth(Drawable... drawables) {
		double width = 0.0;
		for (Drawable d : drawables) {
			if (d == null) {
				continue;
			}
			width = Math.max(width, d.getPreferredSize().getWidth());
		}

		return width;
	}

	/**
	 * Returns the maximum height of an array of Drawables.
	 * @param drawables Drawables to be measured.
	 * @return Maximum vertical extent.
	 */
	private static double getMaxHeight(Drawable... drawables) {
		double height = 0.0;
		for (Drawable d : drawables) {
			if (d == null) {
				continue;
			}
			height = Math.max(height, d.getPreferredSize().getHeight());
		}

		return height;
	}

	/**
	 * Sets the bounds of the specified {@code Drawable} to the specified
	 * values.
	 * @param component {@code Drawable} that should be resized.
	 * @param x X coordinate.
	 * @param y Y coordinate.
	 * @param w Width.
	 * @param h Height.
	 */
	private static void layoutComponent(Drawable component,
			double x, double y, double w, double h) {
		if (component == null) {
			return;
		}
		component.setBounds(x, y, w, h);
	}
}
