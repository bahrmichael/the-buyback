import { BaseEntity } from './../../shared';

export class Contracts implements BaseEntity {
    constructor(
        public id?: number,
        public client?: string,
        public created?: any,
        public price?: number,
        public reward?: number,
        public jitaBuy?: number,
        public jitaSell?: number,
        public declineMailSent?: boolean,
        public approved?: boolean,
        public buybackPrice?: number,
        public appraisalLink?: string,
        public acceptedLocation?: boolean
    ) {
    }
}
