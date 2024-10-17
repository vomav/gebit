import MessageBox from "sap/m/MessageBox";
import Controller from "sap/ui/core/mvc/Controller";
import AppComponent from "../Component";
import UIComponent from "sap/ui/core/UIComponent";
import ODataModel from "sap/ui/model/odata/v4/ODataModel";
import View from "sap/ui/core/mvc/View";
import { URLHelper } from "sap/m/library";
/**
 * @namespace ui5.gebit.app.reuse.public.controller
 */
export default class NotFound extends Controller {

    
	public onInit() : void {
		// apply content density mode to root view
		const view = this.getView() as View
		if (view) {
			view.addStyleClass((this.getOwnerComponent() as AppComponent).getContentDensityClass());
		}
	}
	
}