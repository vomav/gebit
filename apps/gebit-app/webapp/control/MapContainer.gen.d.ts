import { PropertyBindingInfo } from "sap/ui/base/ManagedObject";
import { $ControlSettings } from "sap/ui/core/Control";

declare module "./MapContainer" {

    /**
     * Interface defining the settings object used in constructor calls
     */
    interface $MapContainerSettings extends $ControlSettings {
        boundaryPolygon?: string | PropertyBindingInfo;
        name?: string | PropertyBindingInfo;
        linkForIFrame?: string | PropertyBindingInfo;
        showCurrentLocation?: boolean | PropertyBindingInfo | `{${string}}`;
    }

    export default interface MapContainer {

        // property: boundaryPolygon

        /**
         * Gets current value of property "boundaryPolygon".
         *
         * Default value is: ""
         * @returns Value of property "boundaryPolygon"
         */
        getBoundaryPolygon(): string;

        /**
         * Sets a new value for property "boundaryPolygon".
         *
         * When called with a value of "null" or "undefined", the default value of the property will be restored.
         *
         * Default value is: ""
         * @param [boundaryPolygon=""] New value for property "boundaryPolygon"
         * @returns Reference to "this" in order to allow method chaining
         */
        setBoundaryPolygon(boundaryPolygon: string): this;

        /**
         * Binds property "boundaryPolygon" to model data.
         *
         * See {@link sap.ui.base.ManagedObject#bindProperty ManagedObject.bindProperty} for a
         * detailed description of the possible properties of "oBindingInfo"
         * @param oBindingInfo The binding information
         * @returns Reference to "this" in order to allow method chaining
         */
        bindBoundaryPolygon(bindingInfo: PropertyBindingInfo): this;

        /**
         * Unbinds property "boundaryPolygon" from model data.
         *
         * @returns Reference to "this" in order to allow method chaining
         */
        unbindBoundaryPolygon(): this;

        // property: name

        /**
         * Gets current value of property "name".
         *
         * Default value is: ""
         * @returns Value of property "name"
         */
        getName(): string;

        /**
         * Sets a new value for property "name".
         *
         * When called with a value of "null" or "undefined", the default value of the property will be restored.
         *
         * Default value is: ""
         * @param [name=""] New value for property "name"
         * @returns Reference to "this" in order to allow method chaining
         */
        setName(name: string): this;

        /**
         * Binds property "name" to model data.
         *
         * See {@link sap.ui.base.ManagedObject#bindProperty ManagedObject.bindProperty} for a
         * detailed description of the possible properties of "oBindingInfo"
         * @param oBindingInfo The binding information
         * @returns Reference to "this" in order to allow method chaining
         */
        bindName(bindingInfo: PropertyBindingInfo): this;

        /**
         * Unbinds property "name" from model data.
         *
         * @returns Reference to "this" in order to allow method chaining
         */
        unbindName(): this;

        // property: linkForIFrame

        /**
         * Gets current value of property "linkForIFrame".
         *
         * Default value is: ""
         * @returns Value of property "linkForIFrame"
         */
        getLinkForIFrame(): string;

        /**
         * Sets a new value for property "linkForIFrame".
         *
         * When called with a value of "null" or "undefined", the default value of the property will be restored.
         *
         * Default value is: ""
         * @param [linkForIFrame=""] New value for property "linkForIFrame"
         * @returns Reference to "this" in order to allow method chaining
         */
        setLinkForIFrame(linkForIFrame: string): this;

        /**
         * Binds property "linkForIFrame" to model data.
         *
         * See {@link sap.ui.base.ManagedObject#bindProperty ManagedObject.bindProperty} for a
         * detailed description of the possible properties of "oBindingInfo"
         * @param oBindingInfo The binding information
         * @returns Reference to "this" in order to allow method chaining
         */
        bindLinkForIFrame(bindingInfo: PropertyBindingInfo): this;

        /**
         * Unbinds property "linkForIFrame" from model data.
         *
         * @returns Reference to "this" in order to allow method chaining
         */
        unbindLinkForIFrame(): this;

        // property: showCurrentLocation

        /**
         * Gets current value of property "showCurrentLocation".
         *
         * Default value is: true
         * @returns Value of property "showCurrentLocation"
         */
        getShowCurrentLocation(): boolean;

        /**
         * Sets a new value for property "showCurrentLocation".
         *
         * When called with a value of "null" or "undefined", the default value of the property will be restored.
         *
         * Default value is: true
         * @param [showCurrentLocation=true] New value for property "showCurrentLocation"
         * @returns Reference to "this" in order to allow method chaining
         */
        setShowCurrentLocation(showCurrentLocation: boolean): this;

        /**
         * Binds property "showCurrentLocation" to model data.
         *
         * See {@link sap.ui.base.ManagedObject#bindProperty ManagedObject.bindProperty} for a
         * detailed description of the possible properties of "oBindingInfo"
         * @param oBindingInfo The binding information
         * @returns Reference to "this" in order to allow method chaining
         */
        bindShowCurrentLocation(bindingInfo: PropertyBindingInfo): this;

        /**
         * Unbinds property "showCurrentLocation" from model data.
         *
         * @returns Reference to "this" in order to allow method chaining
         */
        unbindShowCurrentLocation(): this;
    }
}
