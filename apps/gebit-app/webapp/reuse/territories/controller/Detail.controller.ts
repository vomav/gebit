import Controller from "sap/ui/core/mvc/Controller";
import UIComponent from "sap/ui/core/UIComponent";
import { Router$RouteMatchedEvent } from "sap/ui/core/routing/Router";
import Event from "sap/ui/base/Event";
import JSONModel from "sap/ui/model/json/JSONModel";
import ODataModel from "sap/ui/model/odata/v4/ODataModel";
/**
 * @namespace ui5.gebit.app.reuse.territories.controller
 */           
export default class Details extends Controller {

	public onInit() : void {
		let router = (this.getOwnerComponent() as UIComponent).getRouter();
		router.attachRouteMatched(this.attachRouteMatched, this);
	}

	public attachRouteMatched(oEvent:Router$RouteMatchedEvent) {
		let routeName = oEvent.getParameter("name");
		if(routeName == "detail") {
			this.matched(oEvent.getParameter("arguments").id);
		}
	}

	public matched(context:string) {
		(this.getView() as any).bindElement("/Territories("+context+")");
		
	}

	public onEditButton(oEvent:Event) {
		(this.getView()?.getModel("uiModel") as JSONModel).setProperty("/ui/editMode", true);
	}

	public onDisplayMode(oEvent:Event) {
		(this.getView()?.getModel("uiModel") as JSONModel).setProperty("/ui/editMode", false);
	}

	public formatLinkToEmbedHtml(link:string) {
		let escapedLink = link.replace("&", "&amp;")
		// return "&lt;iframe src=&quot;" + escapedLink + "&quot width=&quot;100%&quot; height=&quot;480&quot;&gt;&lt;/iframe&gt;"
		return "<iframe src=\"" +link+ "\" width=\"100%\" height=\"480\"></iframe>"
	}

	public onDeleteTerritory(oEvent:Event) {
		let model = (this.getView()?.getModel() as ODataModel);

		let bindingPath = this.getView().getBindingContext().getPath();
		if(bindingPath) 
			model.delete(bindingPath);

		(this.getOwnerComponent() as UIComponent).getRouter().navTo("worklist");
	}

}