export class Formatter {
    public constructor(){}
    public static formatDateColumn(value:string) {
            if(!value) 
                return "";
            
            let date = new Date(value);
            var d = date.getDate();
            var m = date.getMonth() + 1; //Month from 0 to 11
            var y = date.getFullYear();
            return '' + y + '-' + (m<=9 ? '0' + m : m) + '-' + (d <= 9 ? '0' + d : d);
        
    }
}