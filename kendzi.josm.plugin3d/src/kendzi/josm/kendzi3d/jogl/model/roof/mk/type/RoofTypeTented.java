/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk.type;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point2d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.factory.MeshFactory;
import kendzi.josm.kendzi3d.dto.TextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofMaterials;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTypeOutput;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.alias.RoofTypeAliasEnum;
import kendzi.math.geometry.Graham;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.Triangulate;
import kendzi.math.geometry.polygon.MultiPolygonList2d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonWithHolesList2d;

import org.apache.log4j.Logger;

/**
 * Roof type Tented.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class RoofTypeTented extends RectangleRoofTypeBuilder {

    /** Log. */
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(RoofTypeTented.class);

    @Override
    public RoofTypeAliasEnum getPrefixKey() {
        return RoofTypeAliasEnum.TENTED;
    }

    @Override
    public boolean isPrefixParameter() {
        return false;
    }

    @Override
    public RoofTypeOutput buildRectangleRoof(PolygonWithHolesList2d buildingPolygon, Point2d[] rectangleContur,
            double scaleA, double scaleB, double pRecHeight, double pRecWidth, Integer prefixParameter,
            Map<MeasurementKey, Measurement> pMeasurements, RoofMaterials pRoofTextureData) {

        Double h1 = getHeightMeters(pMeasurements, MeasurementKey.HEIGHT_1, 2.5d);

        return build(buildingPolygon, pRecHeight, pRecWidth, rectangleContur, h1, pRoofTextureData);

    }

    @Override
    protected boolean normalizeAB() {
        return false;
    }

    /**
     *
     * @param buildingPolygon
     * @param pBorderList
     * @param pRecHeight
     * @param pRecWidth
     * @param pRectangleContur
     * @param h1
     * @param pRoofTextureData
     * @return
     */
    protected RoofTypeOutput build(PolygonWithHolesList2d buildingPolygon, double pRecHeight, double pRecWidth,
            Point2d[] pRectangleContur, double h1, RoofMaterials pRoofTextureData) {

        MeshFactory meshBorder = createFacadeMesh(pRoofTextureData);
        MeshFactory meshRoof = createRoofMesh(pRoofTextureData);

        TextureData roofTexture = pRoofTextureData.getRoof().getTextureData();

        List<Point2d> outlineList = buildingPolygon.getOuter().getPoints();

        // XXX temporary ?
        if (0.0f > Triangulate.area(outlineList)) {
            outlineList = PolygonList2d.reverse(outlineList);
        }

        List<Point2d> outlineConvexHull = Graham.grahamScan(outlineList);

        Point2d middlePoint = RoofTypePyramidal.findMidlePoint(outlineConvexHull);

        MultiPolygonList2d[] mp = createMP(outlineList, middlePoint);

        Plane3d[] planes = RoofTypePyramidal.createPlanes(outlineList, h1, middlePoint);

        Vector3d[] roofLine = RoofTypePyramidal.createRoofLines(outlineList);

        double[] textureOffset = RoofTypePyramidal.createTextureOffset(outlineList);

        for (int i = 0; i < mp.length; i++) {

            RoofTypeUtil
                    .addPolygonToRoofMesh(meshRoof, mp[i], planes[i], roofLine[i], roofTexture, textureOffset[i], 0);

        }

        RoofTypeOutput rto = new RoofTypeOutput();
        rto.setHeight(h1);

        rto.setMesh(Arrays.asList(meshBorder, meshRoof));

        return rto;
    }

    /**
     * Create roof surface polygons.
     *
     * @param outlinePolygon
     * @param middlePoint
     * @return
     */
    private MultiPolygonList2d[] createMP(List<Point2d> outlinePolygon, Point2d middlePoint) {

        int size = outlinePolygon.size();

        MultiPolygonList2d[] ret = new MultiPolygonList2d[size];

        for (int i = 0; i < size; i++) {
            Point2d p1 = outlinePolygon.get(i);
            Point2d p2 = outlinePolygon.get((i + 1) % size);

            ret[i] = new MultiPolygonList2d(new PolygonList2d(p1, p2, middlePoint));
        }

        return ret;
    }
}
