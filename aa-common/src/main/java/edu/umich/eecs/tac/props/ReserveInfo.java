package edu.umich.eecs.tac.props;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

import java.text.ParseException;

/**
 * @author Patrick Jordan
 */
public class ReserveInfo extends AbstractTransportable {
	private double promotedReserve;
	private double regularReserve;

	public double getPromotedReserve() {
		return promotedReserve;
	}

	public void setPromotedReserve(double promotedReserve) {
		lockCheck();
		this.promotedReserve = promotedReserve;
	}

	public double getRegularReserve() {
		return regularReserve;
	}

	public void setRegularReserve(double regularReserve) {
		lockCheck();
		this.regularReserve = regularReserve;
	}

	protected void readWithLock(TransportReader reader) throws ParseException {
		promotedReserve = reader.getAttributeAsDouble("promotedReserve", 0.0);
		regularReserve = reader.getAttributeAsDouble("regularReserve", 0.0);
	}

	protected void writeWithLock(TransportWriter writer) {
		writer.attr("promotedReserve", promotedReserve);
		writer.attr("regularReserve", regularReserve);
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		ReserveInfo that = (ReserveInfo) o;

		if (Double.compare(that.promotedReserve, promotedReserve) != 0)
			return false;
		if (Double.compare(that.regularReserve, regularReserve) != 0)
			return false;

		return true;
	}

	public int hashCode() {
		int result;
		long temp;
		temp = promotedReserve != +0.0d ? Double
				.doubleToLongBits(promotedReserve) : 0L;
		result = (int) (temp ^ (temp >>> 32));
		temp = regularReserve != +0.0d ? Double
				.doubleToLongBits(regularReserve) : 0L;
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
}
