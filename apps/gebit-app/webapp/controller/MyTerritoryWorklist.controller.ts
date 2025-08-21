import Controller from "sap/ui/core/mvc/Controller";
import AppComponent from "../Component";
import View from "sap/ui/core/mvc/View";
import ColumnListItem from "sap/m/ColumnListItem";
import UIComponent from "sap/ui/core/UIComponent";
import { Router$RouteMatchedEvent } from "sap/ui/core/routing/Router";
import {Formatter} from "./FormatterUtils"
import Table from "sap/m/Table";
import ODataListBinding from "sap/ui/model/odata/v4/ODataListBinding";
import TableTitleSetter from "./utils/TableTitleSetter";
import Title from "sap/m/Title";
/**
 * @namespace ui5.gebit.app.controller
 */           
export default class MyTerritoryWorklist extends Controller {
	isModelInitialized:Boolean;
	isInitialized: Boolean;
	public onInit() : void {
		const view = this.getView() as View
		if (view) {
			view.addStyleClass((this.getOwnerComponent() as AppComponent).getContentDensityClass());
		}

		let router = (this.getOwnerComponent() as UIComponent).getRouter();
		router.attachRouteMatched(this.attachRouteMatched, this);
	}


	public attachRouteMatched(oEvent:Router$RouteMatchedEvent) {
		let routeName = oEvent.getParameter("name");
		if(routeName == "myTerritories" && this.isModelInitialized) {
			this.getView()?.getModel()?.refresh();
		}
		this.isModelInitialized = true;
	}
	
	public onBeforeRendering(): void | undefined {
		if (this.isInitialized) {
			return;
		}
		let table = this.getView()?.byId("myTerritoriesTable") as Table;
		let oBinding = table.getBinding("items") as ODataListBinding;
		new TableTitleSetter(oBinding, this.getView()?.byId("myTerritoryCountTitle") as Title, this.getView()?.getModel("i18n").getResourceBundle(), "territoriesCount");
		this.isInitialized = true;
	}
	
	public onPressListItem(oEvent:Event) {
		let oBindingContext = (oEvent.getSource() as ColumnListItem).getBindingContext();
		let router = (this.getOwnerComponent() as UIComponent).getRouter();
		router.navTo("myTerritoryDetail",{
			"id" : oBindingContext?.getProperty("ID")
		})
	}


	public formatStartEndDate(value:string) {
		return Formatter.formatDateColumn(value);
	}
}