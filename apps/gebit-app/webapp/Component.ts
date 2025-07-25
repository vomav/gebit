import UIComponent from "sap/ui/core/UIComponent";
import Device from "sap/ui/Device";
import ODataModel from "sap/ui/model/odata/v4/ODataModel";


/**
 * @namespace ui5.gebit.app
 */
export default class Component extends UIComponent {

	public static metadata = {
		manifest: "json"
	};

	private contentDensityClass : string;

	public init() : void {
		let model = this.getModel() as ODataModel;
		model.changeHttpHeaders({"Authorization" : "Bearer " +  localStorage.getItem("gebitAccessToken")});

		let uiServiceModel = this.getModel("userAdminModel") as ODataModel;
		uiServiceModel.changeHttpHeaders({"Authorization" : "Bearer " +  localStorage.getItem("gebitAccessToken")});

		super.init();
		this.getRouter().initialize();
		let that = this;
		model.attachDataReceived(function(event:any ) {
			if(JSON.stringify(event.getParameters()).indexOf("403") >= 0 || JSON.stringify(event.getParameters()).indexOf("401") >= 0 || JSON.stringify(event.getParameters()).indexOf("500") >= 0 || JSON.stringify(event.getParameters()).indexOf("404") >= 0) {
				let router = that.getRouter();
				router.navTo("welcome");
			}
		});

		uiServiceModel.attachDataReceived(function(event:any ) {
			if(JSON.stringify(event.getParameters()).indexOf("403") >= 0 || JSON.stringify(event.getParameters()).indexOf("401") >= 0 || JSON.stringify(event.getParameters()).indexOf("500") >= 0 || JSON.stringify(event.getParameters()).indexOf("404") >= 0) {
				let router = that.getRouter();
				router.navTo("welcome");
			}
		})

	}




	/**
	 * This method can be called to determine whether the sapUiSizeCompact or sapUiSizeCozy
	 * design mode class should be set, which influences the size appearance of some controls.
	 *
	 * @public
	 * @return {string} css class, either 'sapUiSizeCompact' or 'sapUiSizeCozy' - or an empty string if no css class should be set
	 */
	public getContentDensityClass() : string {
		if (this.contentDensityClass === undefined) {
			// check whether FLP has already set the content density class; do nothing in this case
			if (document.body.classList.contains("sapUiSizeCozy") || document.body.classList.contains("sapUiSizeCompact")) {
				this.contentDensityClass = "";
			} else if (!Device.support.touch) { // apply "compact" mode if touch is not supported
				this.contentDensityClass = "sapUiSizeCompact";
			} else {
				// "cozy" in case of touch support; default for most sap.m controls, but needed for desktop-first controls like sap.ui.table.Table
				this.contentDensityClass = "sapUiSizeCozy";
			}
		}
		return this.contentDensityClass;
	}

}