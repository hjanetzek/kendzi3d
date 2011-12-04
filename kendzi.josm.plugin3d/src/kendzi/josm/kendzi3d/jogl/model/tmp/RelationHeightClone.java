package kendzi.josm.kendzi3d.jogl.model.tmp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kendzi.josm.kendzi3d.jogl.ModelUtil;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;

public class RelationHeightClone implements java.lang.Iterable<Double> {

    private double offset = 0;

    private int repeat = 0;

    private double every = 0;

    public static List<RelationHeightClone> buildHeightClone(Way pWay) {
        List<RelationHeightClone> ret = new ArrayList<RelationHeightClone>();

        if (pWay == null) {
            return ret;
        }

        for (OsmPrimitive op : pWay.getReferrers()) {
            if (op instanceof Relation) {
                Relation r = (Relation) op;

                if ("clone:height".equals(op.get("type"))) {
                    RelationHeightClone clone2 = parseHeightClone(r);

                    if (clone2 != null) {
                        ret.add(clone2);
                    }


                }
//                    for (RelationMember rm : r.getMembers()) {
//                        if ("building:roof:ridge".equals(rm.getRole())
//                                && rm.isWay()) {
//                            ridgeWay = rm.getWay();
//                            break ref;
//                        }
//                    }
            }
        }

        return ret;
    }

    private static RelationHeightClone parseHeightClone(Relation pRelation) {
        if (pRelation == null) {
            return null;
        }

        RelationHeightClone clone = new RelationHeightClone();
        clone.setOffset(
                ModelUtil.parseHeight(pRelation.get("offset"), 0d));

        clone.setRepeat(0);
        try {
            clone.setRepeat(
                Integer.parseInt(pRelation.get("repeat")));
        } catch (Exception e) {
            //
        }
        clone.setEvery(
                ModelUtil.parseHeight(pRelation.get("every"), 0d));

        return clone;

    }

    /**
     * @return the offset
     */
    public double getOffset() {
        return this.offset;
    }

    /**
     * @param offset the offset to set
     */
    public void setOffset(double offset) {
        this.offset = offset;
    }

    /**
     * @return the repeat
     */
    public int getRepeat() {
        return this.repeat;
    }

    /**
     * @param repeat the repeat to set
     */
    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    /**
     * @return the every
     */
    public double getEvery() {
        return this.every;
    }

    /**
     * @param every the every to set
     */
    public void setEvery(double every) {
        this.every = every;
    }

    @Override
    public Iterator<Double> iterator() {
        Iterator<Double> ret = new Iterator<Double>() {

            int loop = 0;

            @Override
            public boolean hasNext() {
                return this.loop < RelationHeightClone.this.repeat;
            }

            @Override
            public Double next() {
                if (!hasNext()) {
                    return null;
                }
                this.loop++;
                return this.loop * RelationHeightClone.this.every + RelationHeightClone.this.offset ;
            }

            @Override
            public void remove() {
                throw new RuntimeException("method remove is not allowed");
            }

        };

        return ret;
    }



}