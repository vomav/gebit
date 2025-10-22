import Control from "sap/ui/core/Control";
import RenderManager from "sap/ui/core/RenderManager";
import { MetadataOptions } from "sap/ui/core/Element";
import HTML from "sap/ui/core/HTML";
import Element from "sap/ui/core/Element";

/**
 * @namespace ui5.gebit.app.control
 */
export default class MapContainer extends Control {

  static readonly metadata: MetadataOptions = {
    properties: {
      boundaryPolygon: { type: "string", defaultValue: "", bindable: true },
      polygons: { type: "string", defaultValue: "", bindable: true },
      name: { type: "string", defaultValue: "", bindable: true },
      linkForIFrame: { type: "string", defaultValue: "", bindable: true },
      showCurrentLocation: { type: "boolean", defaultValue: true, bindable: true },
      compact: { type: "boolean", defaultValue: false, bindable: true }
    },
    
    aggregations: {
      _iframe: { type: "sap.ui.core.HTML", multiple: false, visibility: "hidden" }
    }
  };

  private _olMap?: any;
  private _currentLocationLayer?: any;
  private _vectorLayer: any;
  private lastStrokeColor: string;
  private lastFillColor: string;

  onAfterRendering(): void {
    const id = this.getId();
    const boundaryPolygonArray = this.getProperty("boundaryPolygon");
    const polygonsArray = this.getProperty("polygons");
    if (this.getProperty("linkForIFrame")) return;
    if (!polygonsArray) return;


    // Delay to ensure the DOM is fully sized
    setTimeout(() => {
        //   const mapDiv = Element.getElementById(id);
        this.addPolygon('', boundaryPolygonArray);
        this.addPolygon(this.getProperty("name") || "Polygon", polygonsArray);
    }, 150);
  }

  public addPolygon(name: string, coordinates: string): void {
        const mapDiv = document.getElementById(this.getId());
        let coords : number[][] = [];
        try {
            coords= JSON.parse(coordinates);
        } catch (e) {
            console.error("Invalid coordinates JSON:", e);
        return;
        }

        let feature = this.buildFeature(name, coords);
        
        if(!this._vectorLayer) {
            this._vectorLayer = new ol.layer.Vector({
                source: new ol.source.Vector({ features: [feature] })
            });
        } else {
            this._vectorLayer.getSource().addFeature(feature);
        }
        if(!this._olMap) {
            // const projectedCoords = coords.map(([lat, lon]) => ol.proj.fromLonLat([lon, lat]));
            const projectedCoords = coords.map(([lat, lon]) => ol.proj.fromLonLat([lat, lon]));
            this._olMap = new ol.Map({
                target: mapDiv,
                layers: [
                new ol.layer.Tile({ source: new ol.source.OSM() }),
                this._vectorLayer
                ],
                view: new ol.View({
                center: projectedCoords[0],
                zoom: 5
                })
            });
        } else {
            // Fit map to show all polygons
            const extent = this._vectorLayer.getSource().getExtent();
            this._olMap.getView().fit(extent, { padding: [40, 40, 40, 40], duration: 800 });
        }

        
      if (this.getProperty("showCurrentLocation")) {
        this._showCurrentLocation(this._olMap);
      }
    
    }

    private buildFeature(name:string, coords: number[][]): any {
      const projectedCoords = coords.map(([lat, lon]) => ol.proj.fromLonLat([lon, lat]));
      const polygon = new ol.geom.Polygon([projectedCoords]);
      const feature = new ol.Feature(polygon);

      feature.setStyle(this.buildNextStyle(name));

      return feature;
  } 

  private buildNextStyle(name:string): any {
    this.lastStrokeColor = this.lastStrokeColor === undefined ? "#007AFF" : this.getDifferentHexColor(this.lastStrokeColor);

    
    this.lastFillColor = this.lastFillColor === undefined ? "#007AFF"  : this.getDifferentHexColor(this.lastFillColor, 0.15);
    return new ol.style.Style({
            stroke: new ol.style.Stroke({ color: this.lastStrokeColor, width: 1 }),
            fill: new ol.style.Fill({ color: this.lastFillColor}),
            text: this.buildText(name)
        })
  }

  private getDifferentHexColor(hexColor, alpha = 1) {
  if (typeof hexColor !== "string") {
    throw new Error("Color must be a string in hex format.");
  }

  // Normalize and clean up input
  let hex = hexColor.trim().replace(/^#/, "").toLowerCase();

  // Expand shorthand (e.g. #0af → #00aaff)
  if (hex.length === 3) {
    hex = hex.split("").map(c => c + c).join("");
  }



  // Parse RGB
  const r = parseInt(hex.substring(0, 2), 16);
  const g = parseInt(hex.substring(2, 4), 16);
  const b = parseInt(hex.substring(4, 6), 16);

  // Compute complementary color (inverted RGB)
  const newR = 255 - r;
  const newG = 255 - g;
  const newB = 255 - b + 20; // Slightly adjust blue for variety

  // Helper: convert number → 2-digit hex
  const toHex = (val) => val.toString(16).padStart(2, "0");

  // Build base hex
  let result = `#${toHex(newR)}${toHex(newG)}${toHex(newB)}`;

  // Add alpha if < 1
  if (alpha < 1) {
    const alphaHex = toHex(Math.round(alpha * 255));
    result += alphaHex;
  }

  return result.toUpperCase();
}

  private buildText(name:string): any {
      return new ol.style.Text({
        text: name,
        font: "bold 14px sans-serif",
        fill: new ol.style.Fill({ color: "#000" }),
        stroke: new ol.style.Stroke({ color: "#fff", width: 3 })
      });
  };

  /**
   * Add user's current location marker to the map
   */
  private _showCurrentLocation(map: any): void {
    if (!navigator.geolocation) {
      console.warn("Geolocation not supported by this browser.");
      return;
    }

    navigator.geolocation.getCurrentPosition(
      (pos) => {
        const { latitude, longitude, accuracy } = pos.coords;
        // const userPosition = ol.proj.fromLonLat([longitude, latitude]);
        const userPosition = ol.proj.fromLonLat([latitude,longitude]);

        const pointFeature = new ol.Feature(new ol.geom.Point(userPosition));

        pointFeature.setStyle(new ol.style.Style({
          image: new ol.style.Circle({
            radius: 6,
            fill: new ol.style.Fill({ color: "#007aff" }),
            stroke: new ol.style.Stroke({ color: "#fff", width: 2 })
          })
        }));

        const accuracyCircle = new ol.Feature(
          new ol.geom.Circle(userPosition, accuracy)
        );

        accuracyCircle.setStyle(new ol.style.Style({
          fill: new ol.style.Fill({ color: "rgba(0,122,255,0.1)" }),
          stroke: new ol.style.Stroke({ color: "rgba(0,122,255,0.4)", width: 1 })
        }));

        const locationLayer = new ol.layer.Vector({
          source: new ol.source.Vector({ features: [accuracyCircle, pointFeature] })
        });

        map.addLayer(locationLayer);
        this._currentLocationLayer = locationLayer;

        // Optionally recenter map to user
        map.getView().animate({ center: userPosition, zoom: 13, duration: 800 });
      },
      (err) => console.warn("Unable to retrieve location:", err),
      { enableHighAccuracy: true, timeout: 5000 }
    );
  }

  renderer = {
    apiVersion: 4,
    render: (rm: RenderManager, control: MapContainer) => {
      rm.openStart("div", control);
      rm.style("width", "100%");
      if(control.getProperty("compact")) {
        rm.style("height", "400px");
      } else {
        rm.style("height", "600px");
      }
      rm.class("ol-map");
      rm.openEnd();

      if (control.getProperty("linkForIFrame")) {
        const iframeHtml = `<iframe src="${control.getProperty("linkForIFrame")}" width="100%" height="100%" style="border:none;"></iframe>`;
        control.setAggregation("_iframe", new HTML({ content: iframeHtml }), true);
        rm.renderControl(control.getAggregation("_iframe") as Control);
      }

      rm.close("div");

      if (control._olMap) {
        control._olMap.setTarget(undefined);
        control._olMap = undefined;
      }
    }
  }
}
