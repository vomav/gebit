import MessageBox from "sap/m/MessageBox";
import Controller from "sap/ui/core/mvc/Controller";
import AppComponent from "../../public/Component";
import UIComponent from "sap/ui/core/UIComponent";
import ODataModel from "sap/ui/model/odata/v4/ODataModel";
import View from "sap/ui/core/mvc/View";
import { URLHelper } from "sap/m/library";
import Control from "sap/ui/core/Control";
import { Router$RouteMatchedEvent } from "sap/ui/core/routing/Router";
import { ObjectBindingInfo } from "sap/ui/base/ManagedObject";
import Table from "sap/m/Table";
import Context from "sap/ui/model/Context";
import ColumnListItem from "sap/m/ColumnListItem";
import Text from "sap/m/Text";

/**
 * @namespace ui5.gebit.app.reuse.sites.controller
 */           
export default class SitesWorklist extends Controller {

	public onInit() : void {
		// apply content density mode to root view
		const view = this.getView() as View
		if (view) {
			view.addStyleClass((this.getOwnerComponent() as AppComponent).getContentDensityClass());
		}

        let router = (this.getOwnerComponent() as UIComponent).getRouter();
		router.attachRouteMatched(this.attachRouteMatched, this);
	}

    public attachRouteMatched(oEvent:Router$RouteMatchedEvent) {
		let routeName = oEvent.getParameter("name");
		if(routeName == "sites") {
			console.log("refreshWorklist");
		}
      
	}

	public onFilterSelect (oEvent:Event) {
		let sKey = oEvent.getParameter("key");
		let table = this.getView()?.byId("sitesWorklist") as Table;
		

		let cli = new ColumnListItem({
			cells: [
				new Text({
					text: "{name}"
				}),
				new Text({
					text: "{description}"
				}),
				new Text({
					text: "{type}"
				})
			]
		});
		table.bindItems({
			path: "/" + sKey,
			template: cli
		})
			
		
	}

	public onPressListItem(oEvent:Event) {
		let oBindingContext = (oEvent.getSource() as ColumnListItem).getBindingContext();
		let router = (this.getOwnerComponent() as UIComponent).getRouter();
		router.navTo("site",{
			"id" : oBindingContext?.getProperty("ID")
		})
	}
}